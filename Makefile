runScala: scalaJvmBin
	hyperfine \
		--export-markdown report.md \
		--output=./output.txt \
		"scala-io-benchmark-0.0.1-SNAPSHOT/bin/fs-2-wc raw-split" \
		"scala-io-benchmark-0.0.1-SNAPSHOT/bin/fs-2-wc raw-split-reduced" \
		"scala-io-benchmark-0.0.1-SNAPSHOT/bin/fs-2-read-bytes raw-split" \
		"scala-io-benchmark-0.0.1-SNAPSHOT/bin/fs-2-read-bytes raw-split-reduced" \


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
