#!/usr/bin/env python3
with open("/tmp/file", "rb") as fp:
    result = fp.read(64 * 1024)
# assert len(result) == 64 * 1024 * 1024
