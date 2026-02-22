#!/bin/sh
#
# Gradle start up script for UN*X
#

# Attempt to set APP_HOME
PRG="$0"
while [ -h "$PRG" ]; do
  ls=$(ls -ld "$PRG")
  link=$(expr "$ls" : '.*-> \(.*\)$')
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=$(dirname "$PRG")"/$link"
  fi
done
SAVED="$(pwd)"
cd "$(dirname \"$PRG\")/" >/dev/null
APP_HOME="$(pwd -P)"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

MAX_FD="maximum"
warn() { echo "$*"; }
die() { echo; echo "$*"; echo; exit 1; }

OS=$(uname)
case $OS in
  MINGW* | CYGWIN*)
    GRADLE_OPTS=$(cmd //C "echo $GRADLE_OPTS")
    ;;
esac

GRADLE_OPTS="$GRADLE_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\""

if [ ! -f "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" ]; then
  if command -v wget >/dev/null 2>&1; then
    wget -q "https://github.com/nicowillis/gradle-wrapper/releases/download/v0.0.1/gradle-wrapper.jar" -O "$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
  elif command -v curl >/dev/null 2>&1; then
    curl -sSL "https://github.com/nicowillis/gradle-wrapper/releases/download/v0.0.1/gradle-wrapper.jar" -o "$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
  fi
fi

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
JAVACMD="java"
if [ -n "$JAVA_HOME" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
fi

exec "$JAVACMD" \
  $DEFAULT_JVM_OPTS \
  $JAVA_OPTS \
  $GRADLE_OPTS \
  "-Dorg.gradle.appname=$APP_BASE_NAME" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"
