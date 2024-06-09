#!/usr/bin/env python3
import opendal
import asyncio

async def read():
    op = opendal.AsyncOperator("fs", root=str("/tmp"))
    return op.read("file")

result = asyncio.run(read())
# assert len(result) == 64 * 1024 * 1024
