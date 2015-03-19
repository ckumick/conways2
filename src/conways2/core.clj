(ns conways2.core
  (:gen-class)
  (:import [java.awt Graphics Color Dimension BorderLayout]
           [javax.swing JPanel JFrame JButton SwingUtilities]))

(def emptyBoard
  (into (sorted-map) {}))

(def nOffsets
  '([-1 -1] [-1 0] [-1 1] [0 -1] [0 1] [1 -1] [1 0] [1 1]))

(defn- getCellValue
  [board [x y]]
  (get board [x y]))

(defn- getCellsWithValues
  [board]
  (keys board))

(defn- setCellValue
  [board [x y] value]
  (assoc board [x y] value))

(defn addNeighboursToMatrix
  [board [x y]]
  (defn toAbs [n]
    (let [[nx ny] n]
      (vector (+ x nx) (+ y ny))))
  (defn discardKnownCells [n]
    (let [[nx ny] n]
      (nil? (getCellValue board [nx ny]))))
  (reduce #(setCellValue %1 %2 false) board (filter discardKnownCells (map toAbs nOffsets))))

(defn- createLife
  [board [x y]]
  (addNeighboursToMatrix (setCellValue board [x y] true) [x y]))

(defn createLifeAt [board & points]
  (reduce createLife board points))

(defn isAlive?
  [board [x y]]
  (def cell (getCellValue board [x y]))
  (if (nil? cell)
    false
    cell))

(defn- nCount [board [x y]]
  (defn toAbsPt [n]
    (let [[nx ny] n]
      (vector (+ x nx) (+ y ny))))
  (defn keepAliveCells [n]
    (let [[nx ny] n]
      (= true (getCellValue board [nx ny]))))
  (count (filter keepAliveCells (map toAbsPt nOffsets))))

(defn- isAliveAfterTic?
  [board [x y]]
  (let [count (nCount board [x y])]
    (cond
      (= count 3) true
      (and (= count 2) (isAlive? board [x y])) true
      :else false)))

(defn tic [board]
  (reduce createLifeAt emptyBoard
          (filter #(isAliveAfterTic? board %1)
                  (getCellsWithValues board))))

(def rs 4)
(def ws 200)
(def running (atom true))
(def lifeBoard (atom emptyBoard))

(defn lifePanel []
  (proxy [JPanel] []
    (paintComponent [g]
      (proxy-super paintComponent g)
      (doto g
        (.setColor Color/blue)
        (.fillRect 0 0 (* ws rs) (* ws rs)))
      (doseq [x (range ws) y (range ws)]
        (do
          (if (isAlive? @lifeBoard [x y])
            (doto g
              (.setColor Color/green)
              (.fillRect (* x rs) (* y rs) rs rs))
            (doto g
              (.setColor Color/white)
              (.fillRect (* x rs) (* y rs) rs rs)))
          (doto g
            (.setColor Color/black)
            (.drawRect (* x rs) (* y rs) rs rs)))))))

(defn display []
  (let [panel (lifePanel)
        exitbutton (JButton. "Exit")
        frame (JFrame. "Conway's Game of Life")]
    (doto panel
      (.setFocusable true)
      (.setPreferredSize (Dimension. 710 720))
      (.repaint))
    (doto frame
      (.addWindowListener (proxy [java.awt.event.WindowAdapter] []
                            (windowClosing [event]
                              (do
                                (reset! running false)))))
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.setSize 400 400)
      (.add panel BorderLayout/CENTER)
      (.add exitbutton BorderLayout/SOUTH)
      (.pack)
      (.setVisible true))
    (doto exitbutton
      (.addActionListener (proxy [java.awt.event.ActionListener] []
                            (actionPerformed [event]
                              (do
                                (.setVisible frame false)
                                (.dispose frame)
                                (reset! running false))))))
    (loop []
      (when @running
        (let []
          (do
            (.repaint panel)
            (. Thread sleep 1000)
            (reset! lifeBoard (tic @lifeBoard))
            (recur)))))))

(defn randomBoard [xSize ySize]
  (apply createLifeAt emptyBoard
                (for [x (range xSize) y (range ySize)
                      :when (> 20 (rand-int 100))]
                  [(+ x 50) (+ y 50)])))

(defn -main
  [& args]
  (reset! lifeBoard (randomBoard 100 100))
  (display)
  )