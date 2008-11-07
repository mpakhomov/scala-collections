import org.specs._
import org.scalacheck._

import com.codecommit.collection.HashMap

object HashMapSpecs extends Specification with Scalacheck {
  import Prop._
  
  "it" should {
    "store ints" in {
      val prop = property { src: List[Int] =>
        val map = src.foldLeft(new HashMap[Int, Int]) { (m, v) => m(v) = -v }
        src forall { v => map(v) == -v }
      }
      
      prop must pass
    }
    
    "store strings" in {
      val prop = property { src: List[String] =>
        val map = src.foldLeft(new HashMap[String, Int]) { (m, v) => m(v) = v.length }
        src forall { v => map(v) == v.length }
      }
      
      prop must pass
    }
    
    "preserve values across changes" in {
      val prop = property { (map: HashMap[String, String], ls: List[String], f: (String)=>String) =>
        val newMap = ls.filter(!map.contains(_)).foldLeft(map) { (m, k) => m(k) = f(k) }
        (map forall { case (k, v) => newMap(k) == v }) && (ls forall { v => newMap(v) == f(v) })
      }
      
      prop must pass
    }
  }
  
  implicit def arbHashMap[K](implicit ak: Arbitrary[List[K]]): Arbitrary[HashMap[K, String]] = {
    Arbitrary(for {
      keys <- ak.arbitrary
    } yield keys.foldLeft(new HashMap[K, String]) { (m, k) => m(k) = k.toString })
  }
}
