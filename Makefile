run: venv
	PATH="venv/bin:${PATH}" ./bench --show-output --warmup=3 python-* c-* rust-*

venv:
	python3 -mvenv venv
	venv/bin/pip install opendal

