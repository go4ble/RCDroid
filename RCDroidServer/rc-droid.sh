#!/bin/sh
# /etc/init.d/rc-droid

case "$1" in
  start)
    echo "Starting RCDroidServer"
    ;;
  stop)
    echo "Stopping RCDroidServer"
    ;;
  *)
    echo "Usage: /etc/init.d/rc-droid {start|stop}"
    exit 1
    ;;
esac

exit 0
