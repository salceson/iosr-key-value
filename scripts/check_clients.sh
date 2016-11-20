#!/usr/bin/env bash
COMPLETED=`docker-compose ps | grep "Exit 0" | wc -l`
if [ ${COMPLETED} -ne 3 ] ; then
    echo "Completed ${COMPLETED} but expected 3."
    exit 1
fi
exit 0
