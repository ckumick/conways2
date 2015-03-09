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
  (let [row (board x)]
    (if (nil? row)
      nil
      (row y))))

(defn- setCellValue
  [board [x y] value]
  (if (contains? board x)
    (assoc board x (assoc (get board x) y value))
    (assoc board x (into (sorted-map) {y value}))))

(defn addNeighboursToMatrix
  [board [x y]]
  (defn toAbs [n]
    (let [[nx ny] n]
      (vector (+ x nx) (+ y ny))))
  (defn discardKnownCells [n]
    (let [[nx ny] n]
      (nil? (getCellValue board [nx ny]))))
  (loop [b board
         p (filter discardKnownCells (map toAbs nOffsets))]
    (if (empty? p)
      b
      (recur (setCellValue b (first p) false) (rest p)))))

(defn- createLife
  [board [x y]]
  (setCellValue board [x y] true))

(defn createLifeAt [board & points]
  (loop [b board
         p points]
    (if (empty? p)
      b
      (recur (addNeighboursToMatrix (createLife b (first p)) (first p)) (rest p)))))

(defn isAlive?
  [board [x y]]
  (def cell (getCellValue board [x y]))
  (if (nil? cell)
    false
    cell))

(defn- nCount [board [x y]]
  (defn toAbs [n]
    (let [[nx ny] n]
      (vector (+ x nx) (+ y ny))))
  (defn keepAliveCells [n]
    (let [[nx ny] n]
      (= true (getCellValue board [nx ny]))))
  (count (filter keepAliveCells (map toAbs nOffsets))))

(defn tic [board]
  (loop [b emptyBoard
         x board]
    (if (empty? x)
      b
      (let [[kx vx] (first x)]
        (recur
          (loop [by b
                 y vx]
            (if (empty? y)
              by
              (let [[ky vy] (first y)
                    count (nCount board [kx ky])]
                (cond
                  (= count 3) (recur (createLifeAt by [kx ky]) (rest y))
                  (and (isAlive? board [kx ky]) (= count 2)) (recur (createLifeAt by [kx ky]) (rest y))
                  :else (recur by (rest y))))))
          (rest x))))))

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
            (. Thread sleep 100)
            (reset! lifeBoard (tic @lifeBoard))
            (recur)))))))

(defn randomBoard [xSize ySize]
  (apply createLifeAt emptyBoard
                (for [x (range xSize) y (range ySize)
                      :when (> 20 (rand-int 100))]
                  [x y])))

(defn -main
  [& args]
  (reset! lifeBoard (randomBoard 100 100))
  (display)
  )