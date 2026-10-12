#!/bin/sh
# Minimal Maven wrapper for Mac/Linux: downloads Apache Maven once, then runs it.
MVN_VER=3.9.9
MVN_ROOT="$HOME/.m2/wrapper/dists"
MVN_HOME="$MVN_ROOT/apache-maven-$MVN_VER"
if [ ! -x "$MVN_HOME/bin/mvn" ]; then
  echo "Downloading Apache Maven $MVN_VER (first run only)..."
  mkdir -p "$MVN_ROOT"
  curl -fsSL "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MVN_VER/apache-maven-$MVN_VER-bin.tar.gz" | tar -xz -C "$MVN_ROOT" || exit 1
fi
exec "$MVN_HOME/bin/mvn" "$@"
