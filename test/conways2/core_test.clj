(ns conways2.core-test
  (:require [clojure.test :refer :all]
            [conways2.core :refer :all]))

(deftest conwayGameOfLife
  (testing "Live cell with no nieghbours dies"
    (def loneCell (createLifeAt emptyBoard [1 1]))
    (is (= false (isAlive? (tic loneCell) [1 1]))))

  (testing "Two live cells with no nieghbours dies"
    (def twoSideBySideCells (createLifeAt emptyBoard [1 2] [1 1]))
    (is (= false (isAlive? (tic twoSideBySideCells) [1 1])))
    (is (= false (isAlive? (tic twoSideBySideCells) [1 2]))))

  (testing "Dead cell by 3 live cells comes to life"
    (is (= true (isAlive? (tic (createLifeAt emptyBoard [1 1] [1 2] [2 1])) [2 2])) "3-L")
    (is (= true (isAlive? (tic (createLifeAt emptyBoard [1 1] [1 2] [1 3])) [0 2])) "3-line")
    (is (= true (isAlive? (tic (createLifeAt emptyBoard [0 0] [0 2] [2 2])) [1 1])) "3-corners")
    (is (= true (isAlive? (tic (createLifeAt emptyBoard [0 1] [1 0] [2 1])) [1 1])) "3-sides")
    )

)

(run-tests)
