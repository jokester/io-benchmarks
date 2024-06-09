run: venv
	PATH="venv/bin:${PATH}" ./bench --show-output --warmup=3 python-* c-* rust-*

venv:
	python3 -mvenv venv
	venv/bin/pip install opendal

scalaJvmBin:
	rm -rf scala-io-benchmark*
	sbt jvmDemo/packageZipTarball
	tar xf scala-multi/target/universal/scala-io-benchmark-0.0.1-SNAPSHOT.tgz

scalaNativeIMage:
	sbt jvmDemo/graalvm-native-image:???
