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
  state)

(defn place-bomb
  [state player]
  state)

(defn explode-bomb
  [state player]
  state)