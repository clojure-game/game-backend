(ns game.api
  (:require [game.core :refer [get-blocks
                               get-player-pos
                               get-bomb]]))

(defn translate-game [state]
  (vec (concat
        (map
         (fn [block] (assoc block :type :block))
         (get-blocks state))
        (map (fn [player] {:pos (get-player-pos state player)
                           :type player}) [:p1 :p2])
        (map (fn [player] (let [player-bomb (get-bomb state player)]
                            (when player-bomb {:pos player-bomb
                                               :type :bomb}))) [:p1 :p2]))))