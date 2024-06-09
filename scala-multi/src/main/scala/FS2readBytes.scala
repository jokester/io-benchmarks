import cats.effect.*
import cats.syntax.all.*
import fs2.*
import fs2.io.file.*

import java.nio.file.{Files as NFiles, Path as NPath}
import java.time.ZonedDateTime
import scala.jdk.javaapi.CollectionConverters;

object FS2readBytes extends IOApp {

  def expandPathnames(pathnames: Seq[String]): IO[Seq[NPath]] = {
    def expandEntry(n: NPath): Seq[NPath] = {
      if (NFiles.isDirectory(n)) {
        CollectionConverters.asScala(NFiles.list(n).toList.iterator()).toSeq
      } else {
        Seq(n)
      }
    }

    IO {
      pathnames.map(str => NPath.of(str)).flatMap(expandEntry)
    }
  }

  def readFile(fn: Path, chunkSize: Option[Int]): Stream[IO, Byte] = {
    Files[IO].readAll(fn, chunkSize.getOrElse(256 * 1024), Flags.Read)
  }

  def sizeFile(fn: Path, chunkSize: Option[Int] = None): IO[Long] = {
    val bytes = readFile(fn, chunkSize)
    val chunkSizes = bytes.chunkMin(1024 * 1024).map(_.size.toLong)
    chunkSizes.compile.toVector.map(_.sum)
  }

  def sizeFiles(fns: Seq[String]): IO[Seq[(String, Long)]] = {
    for (
      entries <- expandPathnames(fns);
      sizes <- entries.traverse(entry => sizeFile(Path.fromNioPath(entry)))
    ) yield {
      entries.zip(sizes).map(pair => (pair._1.toString, pair._2))
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val start = ZonedDateTime.now()
    for (
      result <- sizeFiles(args);
      _ <- IO {
        val elapsed = ZonedDateTime.now().toEpochSecond - start.toEpochSecond
        val bytesPerSec = 1.0 * result.map(_._2).sum / elapsed
        result.foreach((pair) => println(s"${pair._1} : ${pair._2} bytes"))
        println("%.3e bytes/s".format(bytesPerSec))
      }
    ) yield ExitCode.Success
  }
}
