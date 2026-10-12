"""Turns the data in your old Flask SQLite file into MySQL INSERT statements (opens the SQLite file read-only).

Usage (from the backend folder):
    python database/export_sqlite_to_mysql.py path\\to\\database.db
Then run the generated database/import_existing_data.sql in MySQL.
"""
import sqlite3
import sys
from pathlib import Path


def q(v):
    return "'" + str(v).replace("\\", "\\\\").replace("'", "''") + "'"


def main():
    if len(sys.argv) < 2:
        sys.exit("Give the path to your old database.db, e.g.  python database/export_sqlite_to_mysql.py ..\\..\\database.db")
    src = Path(sys.argv[1])
    if not src.exists():
        sys.exit(f"File not found: {src}")
    con = sqlite3.connect(f"file:{src.resolve().as_posix()}?mode=ro", uri=True)
    rows = con.execute("SELECT date, name, prepared, served, COALESCE(donated, 0) FROM food ORDER BY id").fetchall()
    out = Path(__file__).with_name("import_existing_data.sql")
    lines = ["USE food_wastage;"]
    for d, n, p, s, dn in rows:
        lines.append("INSERT INTO food (`date`, name, category, prepared, served, donated) "
                     f"VALUES ({q(d)}, {q(n)}, 'General', {float(p)}, {float(s)}, {float(dn)});")
    out.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote {len(rows)} rows to {out}")


if __name__ == "__main__":
    main()
