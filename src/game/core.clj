(ns game.core)

(defn get-size
  [state] (:size state))

(defn get-blocks
  [state]
  (-> state
      :board
      :blocks))

(defn set-blocks
  [state blocks]
  (assoc-in state [:board :blocks] blocks))

(defn get-bomb
  [state player]
  (get-in state [:players player :bomb_pos]))

(defn add-bomb
  [state player pos]
  (assoc-in state [:players player :bomb_pos] pos))

(defn remove-bomb
  [state player]
  (assoc-in state [:players player :bomb_pos] nil))

(defn get-player-pos
  [state player]
  (get-in state [:players player :pos]))

(defn set-player-pos
  [state player pos]
  (assoc-in state [:players player :pos] pos))

(defn kill-player
  [state player]
  (->
   (set-player-pos state player nil)
   (assoc-in [:players player :alive?] false)))

(defn player-alive?
  [state player]
  (get-in state [:players player :alive?]))

(defn blocked?
  [state pos]
  (some (fn [block-positions] (= pos
                                 (:pos block-positions)))
        (get-blocks state)))

; API functions

(defn create-game
  ([]
   (create-game 20))
  ([size]
   {:size size
    :players {:p1 {:pos {:x 0
                         :y 0}
                   :bomb_pos nil
                   :alive? true}
              :p2 {:pos {:x (dec size)
                         :y (dec size)}
                   :bombs_pos nil
                   :alive? true}}
    :board     {:blocks (concat (for [x (range size)
                                      y (range 4 (- size 4))]
                                  {:pos {:x x :y y}})
                                (for [x (range 4 (- size 4))
                                      y (range size)]
                                  {:pos {:x x :y y}}))}}))

(defn move
  [state player direction]
  {:pre [(some #(= direction %) [:up :down :left :right])
         (some #(= player %) [:p1 :p2])]}
  (if (not (player-alive? state player)) ; Cannot move if dead
    state
    (let [current-pos (get-player-pos state player)
          move-amount 1
          stopoverflow (fn [compare-function edge-pos new-pos]
                         (if (compare-function edge-pos new-pos) edge-pos new-pos))
          change (cond
                   (= direction :up)
                   [:y (stopoverflow > 0 (- (current-pos :y) move-amount))]
                   (= direction :down)
                   [:y (stopoverflow < (dec (get-size state)) (+ (current-pos :y) move-amount))]
                   (= direction :left)
                   [:x (stopoverflow > 0 (- (current-pos :x) move-amount))]
                   (= direction :right)
                   [:x (stopoverflow < (dec (get-size state)) (+ (current-pos :x) move-amount))])
          new-pos (apply assoc current-pos change)]
      (if-not (blocked? state new-pos)
        (set-player-pos state player new-pos)
        state))))

(defn place-bomb
  [state player]
  (let [player-pos (get-player-pos state player)]
    (add-bomb state player player-pos)))

(defn explode-bomb
  [state player]
  (let [bomb (get-bomb state player)]
    (if-not bomb
      state
      (let [bomb-x (:x bomb)
            bomb-y (:y bomb)
            size (get-size state)
            legal-pos? (fn [pos] (and (<= 0 pos) (> size pos)))
            range-fn (fn [axis-pos length]
                       (filter legal-pos?
                               (range (- axis-pos length)
                                      (+ axis-pos (inc length)))))
            x-range (range-fn bomb-x 2)
            y-range (range-fn bomb-y 2)
            y-core-range (range-fn bomb-y 1)
            x-core-range (range-fn bomb-x 1)

            bombed-positions (set (concat (for [x x-range] {:x x :y bomb-y})
                                          (for [y y-range] {:x bomb-x :y y})
                                          (for [x x-core-range y y-core-range] {:x x :y y})))
            kill-player-if-bombed (fn [state player]
                                    (if (contains? bombed-positions
                                                   (get-player-pos state player))
                                      (kill-player state player)
                                      state))
            remove-exploded-blocks (fn [state]
                                     (set-blocks state
                                                 (filter (fn [block]
                                                           (not (contains? bombed-positions (:pos block))))
                                                         (get-blocks state))))]
        (as-> state new-state
          (remove-exploded-blocks new-state)
          (kill-player-if-bombed new-state :p1)
          (kill-player-if-bombed new-state :p2)
          (remove-bomb new-state player))))))