import scala.annotation.tailrec 

object Chapter3 {

  sealed trait List[+A]
  case object Nil extends List[Nothing]
  case class Cons[+A](head: A, tail: List[A]) extends List[A]

  object List {
  
    def sum(ints: List[Int]): Int = ints match {
      case Nil => 0
      case Cons(x, xs) => x + sum(xs)
    }

    def product(ds: List[Double]): Double = ds match {
      case Nil => 1.0
      case Cons(0.0, _) => 0.0
      case Cons(x, xs) => x * product(xs)
    }
  
    def apply[A](as: A*): List[A] = {
      if (as.isEmpty) Nil
      else Cons(as.head, apply(as.tail: _*))
    }

    def tail[A](l: List[A]): List[A] = l match {
      case Nil => Nil
      case Cons(x, xs) => xs
    }

    def setHead[A](l: List[A], h: A): List[A] = l match {
      case Nil => Nil
      case Cons(x, xs) => Cons(h, xs)
    }

    @tailrec def drop[A](l: List[A], n: Int): List[A] = l match {
      case Nil => Nil
      case Cons(x, xs) => {
        if (n <= 0) Cons(x, xs)
        else drop(xs, n - 1)
      }
    }

    @tailrec def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
      case Nil => Nil
      case Cons(x, xs) => {
        if (!f(x)) Cons(x,xs)
        else dropWhile(xs, f)
      }
    }

    def init[A](l: List[A]) : List[A] = l match {
      case Nil => Nil
      case Cons(x, Nil) => Nil
      case Cons(x, xs) => Cons(x, init(xs))
    }

    def foldRight[A, B](as: List[A], z: B)(f: (A, B) => B) : B = {
      as match {
        case Nil => z
        case Cons(x, xs) => f(x, foldRight(xs, z)(f))
      }
    }

    //def productFold(ns: List[Double]) = {
    //  foldRight(ns, 1.0)((x, 0.0) => 0)
                 //(x, y) => x * y)
    
    //}

    def length[C](as: List[C]): Int = {
      //foldRight(as, 0)(_ + 1)
      foldRight(as, 0)((x, y) => (1 + y))
    }

    @tailrec
    def foldLeft[A, B](as: List[A], z: B)(f: (B, A) => B) : B = {
      as match {
        case Nil => z
        case Cons(x, xs) => foldLeft(xs, f(z, x))(f)
        //f(foldLeft(xs, z)(f), x)
      }
    }

    def sumFoldLeft(xs: List[Int]): Int = {
      foldLeft(xs, 0)(_ + _)
    }

    def prodFoldLeft(xs: List[Double]): Double = {
      foldLeft(xs, 1.0)(_ * _)
    }

    def lengthFoldLeft[A](xs: List[A]): Int = {
      foldLeft(xs, 0)((i, xs) => i + 1)
    }

    // Whyyyy?
    def reverse[A](xs: List[A]): List[A] = {
      foldLeft(xs, Nil: List[A])((r, x) => Cons(x, r) : List[A])
    }

    def foldRightLeft[A, B](as: List[A], z: B)(f: (A, B) => B) : B = {
      foldLeft(as, z)((b, a) => f(a, b))
    }

    def sumFoldRightLeft(xs: List[Int]): Int = {
      foldRightLeft(xs, 0)(_ + _)
    }

    def foldLeftRight[A, B](as: List[A], z: B)(f: (B, A) => B) : B = {
      foldRight(as, z)((a, b) => f(b, a))
    }

    def sumFoldLeftRight(xs: List[Int]): Int = {
      foldLeftRight(xs, 0)(_ + _)
    }

    def appendFR[A](as: List[A], bs: List[A]): List[A] = {
      foldRight(as, bs)((x, xs) => Cons(x, xs))
    }
    
    def appendFL[A](as: List[A], bs: List[A]): List[A] = {
      foldLeft(bs, Nil)((xs, x) => Cons(x, as))
    }
  }



  def main(args: Array[String]): Unit = {
    println("3.1. => 3")
    println(List.tail(List("A", "B", "C")))
    println(List.setHead(List("A", "B", "C"), "D"))
    println(List.drop(List(1, 2, 3, 4, 5), 2))
    println(List.dropWhile(List(1, 2, 3, -4, -5), (x: Int) => x > 0))
    println(List.init(List(1, 2, 3, 4, 5)))
    //println(List.productFold(List(1, 2, 3)))
    println(List.foldRight(List(1, 2, 3), Nil:List[Int])(Cons(_, _)))
    println(List.length(List("A", "B", "C")))
    println(List.foldLeft(List(1, 2, 3), Nil:List[Int])((x, y) => Cons(y, x)))
    println(List.sumFoldLeft(List(1, 2, 3)))
    println(List.prodFoldLeft(List(1, 2, 3)))
    println(List.lengthFoldLeft(List(1, 2, 3)))
    println(List.reverse(List(1, 2, 3)))
    println(List.sumFoldRightLeft(List(1, 2, 3)))
    println(List.sumFoldLeftRight(List(1, 2, 3)))
    println(List.appendFR(List(1, 2, 3), List(4, 5)))
    println(List.appendFL(List(1, 2, 3), List(4, 5)))
  }
}
