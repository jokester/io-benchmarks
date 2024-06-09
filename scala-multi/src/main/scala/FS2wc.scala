import cats.effect.*
import cats.syntax.all.*

import java.nio.charset.Charset
import java.nio.file.{Files as NFiles, Path as NPath}
import fs2.io.file.*
import fs2.text
import FS2readBytes.{expandPathnames, readFile}

import java.time.ZonedDateTime

object FS2wc extends IOApp {

  def wcFile(fn: Path, chunkSize: Option[Int] = None): IO[Long] = {
    val bytes = readFile(fn, chunkSize)
    val lines = bytes.through(text.decodeWithCharset(Charset.forName("ASCII"))).through(text.lines)
    val linesCount = lines.chunkMin(1_000).map(_.size.toLong)
    for (
      _ <- IO.println(s"start read ${fn}");
      totalSize <- linesCount.compile.toVector.map(_.sum);
      _ <- IO.println(s"finished read ${fn}")
    ) yield (totalSize)
  }

  def wcFiles(fns: Seq[String]): IO[Seq[(String, Long)]] = {
    for (
      entries <- expandPathnames(fns);
      sizes <- entries.parTraverse(entry => wcFile(Path.fromNioPath(entry)))
    ) yield {
      entries.zip(sizes).map(pair => (pair._1.toString, pair._2))
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val start = ZonedDateTime.now()
    for (
      result <- wcFiles(args);
      _ <- IO {
        val elapsed = ZonedDateTime.now().toEpochSecond - start.toEpochSecond
        val linesPerSec = 1.0 * result.map(_._2).sum / elapsed
        result.foreach((pair) => println(s"${pair._1} : ${pair._2} lines"))
        println("%.3e line/s".format(linesPerSec))
      }
    ) yield ExitCode.Success
  }
}
