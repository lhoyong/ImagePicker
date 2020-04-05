#!/bin/sh

./gradlew bintrayUpload -PbintrayUser="$BINTRAY_USERNAME" -PbintrayKey="$BINTRAY_GPG"