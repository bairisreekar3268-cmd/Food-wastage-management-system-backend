package com.example.foodwaste.service;

import com.example.foodwaste.dto.FoodRequest;
import com.example.foodwaste.dto.FoodResponse;
import com.example.foodwaste.dto.PredictionResponse;
import com.example.foodwaste.dto.SummaryResponse;
import com.example.foodwaste.dto.SummaryResponse.FoodStat;
import com.example.foodwaste.dto.SummaryResponse.Point;
import com.example.foodwaste.dto.SummaryResponse.Suggestion;
import com.example.foodwaste.dto.SummaryResponse.Totals;
import com.example.foodwaste.entity.FoodRecord;
import com.example.foodwaste.exception.ApiException;
import com.example.foodwaste.repository.FoodRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Business rules ported from the Flask app: waste = prepared - served - donated; <10% Normal, <20% Moderate, else High. */
@Service
@Transactional(readOnly = true)
public class FoodService {

    private static final Sort NEWEST = Sort.by(Sort.Direction.DESC, "date", "id");
    private final FoodRepository repo;

    public FoodService(FoodRepository repo) {
        this.repo = repo;
    }

    // ---------- calculation helpers ----------
    static double r1(double v) { return Math.round(v * 10.0) / 10.0; }
    static double r2(double v) { return Math.round(v * 100.0) / 100.0; }
    static String level(double pct) { return pct < 10 ? "Normal" : pct < 20 ? "Moderate" : "High"; }
    static double wasted(FoodRecord f) { return Math.max(f.getPrepared() - f.getServed() - f.getDonated(), 0); }
    static double pct(double wasted, double prepared) { return prepared > 0 ? r1(wasted / prepared * 100) : 0; }

    private FoodResponse toResponse(FoodRecord f) {
        double w = wasted(f);
        double p = pct(w, f.getPrepared());
        return new FoodResponse(f.getId(), f.getDate(), f.getName(), f.getCategory(),
                f.getPrepared(), f.getServed(), f.getDonated(), r2(w), p, level(p));
    }

    // ---------- CRUD ----------
    public List<FoodResponse> list(String q, String category, LocalDate start, LocalDate end) {
        return filtered(q, category, start, end).stream().map(this::toResponse).toList();
    }

    public FoodResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public FoodResponse create(FoodRequest r) {
        FoodRecord f = new FoodRecord();
        apply(f, r);
        return toResponse(repo.save(f));
    }

    @Transactional
    public FoodResponse update(Long id, FoodRequest r) {
        FoodRecord f = find(id);
        apply(f, r);
        return toResponse(repo.save(f));
    }

    @Transactional
    public void delete(Long id) {
        repo.delete(find(id));
    }

    public List<String> categories() {
        return repo.findAll().stream().map(FoodRecord::getCategory).distinct().sorted().toList();
    }

    private FoodRecord find(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Food record " + id + " not found."));
    }

    private void apply(FoodRecord f, FoodRequest r) {
        double donated = r.donated() == null ? 0 : r.donated();
        if (r.served() + donated > r.prepared()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Served + donated cannot exceed prepared.");
        }
        f.setDate(r.date());
        f.setName(r.name().trim());
        f.setCategory(r.category() == null || r.category().isBlank() ? "General" : r.category().trim());
        f.setPrepared(r.prepared());
        f.setServed(r.served());
        f.setDonated(donated);
    }

    private List<FoodRecord> filtered(String q, String category, LocalDate start, LocalDate end) {
        String needle = q == null ? "" : q.trim().toLowerCase();
        return repo.findAll(NEWEST).stream()
                .filter(f -> needle.isEmpty()
                        || f.getName().toLowerCase().contains(needle)
                        || f.getCategory().toLowerCase().contains(needle))
                .filter(f -> category == null || category.isBlank() || f.getCategory().equalsIgnoreCase(category))
                .filter(f -> start == null || !f.getDate().isBefore(start))
                .filter(f -> end == null || !f.getDate().isAfter(end))
                .toList();
    }

    // ---------- analytics ----------
    private FoodStat stat(String name, List<FoodRecord> rs) {
        double p = rs.stream().mapToDouble(FoodRecord::getPrepared).sum();
        double s = rs.stream().mapToDouble(FoodRecord::getServed).sum();
        double d = rs.stream().mapToDouble(FoodRecord::getDonated).sum();
        double w = rs.stream().mapToDouble(FoodService::wasted).sum();
        double pc = pct(w, p);
        return new FoodStat(name, r1(p), r1(s), r1(d), r1(w), pc, level(pc));
    }

    private List<FoodStat> groupStats(List<FoodRecord> rs, Function<FoodRecord, String> key) {
        Map<String, List<FoodRecord>> groups =
                rs.stream().collect(Collectors.groupingBy(key, LinkedHashMap::new, Collectors.toList()));
        List<FoodStat> out = new ArrayList<>();
        groups.forEach((name, list) -> out.add(stat(name, list)));
        out.sort(Comparator.comparingDouble(FoodStat::wasted).reversed());
        return out;
    }

    private List<Point> points(List<FoodRecord> rs, Function<FoodRecord, String> key, int limit) {
        Map<String, double[]> sums = new TreeMap<>();
        for (FoodRecord f : rs) {
            double[] a = sums.computeIfAbsent(key.apply(f), k -> new double[2]);
            a[0] += f.getPrepared();
            a[1] += wasted(f);
        }
        List<Point> out = new ArrayList<>();
        sums.forEach((label, a) -> out.add(new Point(label, r1(a[0]), r1(a[1]))));
        return out.size() > limit ? new ArrayList<>(out.subList(out.size() - limit, out.size())) : out;
    }

    public SummaryResponse summary(LocalDate start, LocalDate end) {
        List<FoodRecord> rs = filtered(null, null, start, end);
        FoodStat t = stat("All", rs);
        Totals totals = new Totals(t.prepared(), t.served(), t.donated(), t.wasted(), t.wastePercent(), t.level(), rs.size());

        LocalDate weekAgo = LocalDate.now().minusDays(7);
        List<FoodRecord> week = rs.stream().filter(x -> !x.getDate().isBefore(weekAgo)).toList();
        List<Suggestion> tips = new ArrayList<>();
        for (FoodStat fs : groupStats(week, FoodRecord::getName)) {
            if (!"Normal".equals(fs.level())) {
                long n = week.stream().filter(x -> x.getName().equals(fs.name())).count();
                double cut = r1(fs.wasted() / Math.max(n, 1));
                tips.add(new Suggestion(fs.level(), fs.name() + " wastage is " + fs.wastePercent()
                        + "% this week. Reduce preparation by about " + cut
                        + " kg per batch and check demand before cooking."));
            }
        }
        return new SummaryResponse(totals,
                points(rs, x -> x.getDate().toString(), 14),
                points(rs, x -> YearMonth.from(x.getDate()).toString(), 6),
                groupStats(rs, FoodRecord::getName),
                groupStats(rs, FoodRecord::getCategory),
                tips);
    }

    /** Recommended preparation = average (served + donated) of the last 7 records + 5% buffer. */
    public PredictionResponse predict(String name) {
        String wanted = name == null ? "" : name.trim();
        List<FoodRecord> rs = repo.findAll(NEWEST).stream()
                .filter(f -> f.getName().equalsIgnoreCase(wanted))
                .limit(7)
                .toList();
        if (rs.size() < 3) {
            return new PredictionResponse(null, rs.size());
        }
        double avg = rs.stream().mapToDouble(f -> f.getServed() + f.getDonated()).average().orElse(0);
        return new PredictionResponse(r1(avg * 1.05), rs.size());
    }
}
