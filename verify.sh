#!/bin/bash

START=$(date +%s)

while [ $(($(date +%s)-$START)) -lt 300 ] && [ `docker-compose ps | grep Up | wc -l` -gt 3 ]; do
    sleep 10
done

docker run --network=iosrkeyvalue_iosr -e="VERIFY_ARGS=member1:8080 member2:8080 member3:8080" iosr/key-value-verify

exit $?
