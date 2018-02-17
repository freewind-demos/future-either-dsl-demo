package example

import cats.data._
import cats.implicits._
import shapeless._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.reflect.ClassTag

object Hello extends App {

  type Error = String
  type ErrorOr[T] = Either[Error, T]
  type FutureEither[A] = EitherT[Future, Error, A]

  object ? {
    def <~[A](x: A): FutureEither[A] = x.pure[FutureEither]
    def <~[A](x: Either[Error, A]): FutureEither[A] = EitherT.fromEither[Future](x)
    def <~[A](x: Future[A])(implicit ev: A <:!< Either[_, _]): FutureEither[A] = EitherT.right(x)
    def <~[A](x: Future[Either[Error, A]]): FutureEither[A] = EitherT(x)
    def <~[A](x: List[Either[Error, A]]): FutureEither[List[A]] = EitherT.fromEither[Future](x.sequence[ErrorOr, A])
    def <~[A, X: ClassTag](x: List[Future[A]]): FutureEither[List[A]] = EitherT.right(x.sequence)
    def <~[A, X: ClassTag, Y: ClassTag](x: List[FutureEither[A]]): FutureEither[List[A]] = x.sequence[FutureEither, A]
  }

  val result = for {
    x1 <- ? <~ 42
    x2 <- ? <~ Right("42")
    x3 <- ? <~ "foo".pure[Future]
    x4 <- ? <~ Right(List("foo", "bar")).pure[Future]
    x5 <- ? <~ List(Right(42), Right(1337))
    x6 <- ? <~ List(32.pure[Future], 42.pure[Future])
    x7 <- ? <~ List("42".pure[FutureEither], "foo".pure[FutureEither])
  } yield {
    println(x1)
    println(x2)
    println(x3)
    println(x4)
    println(x5)
    println(x6)
    println(x7)
  }

  Await.result(result.value, 10.seconds)
}
