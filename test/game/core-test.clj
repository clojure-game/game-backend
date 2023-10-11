(ns game.core-test
  (:require [clojure.test :refer [deftest
                                  is]]
            [game.core :refer [create-game
                               explode-bomb
                               get-bomb
                               get-player-pos
                               get-size
                               player-alive?
                               move
                               place-bomb]]))

(deftest move-right-player-1
  []
  (is (= (-> (create-game)
             (move :p1 :right)
             (get-player-pos :p1))
         {:x 1 :y 0})))

(deftest move-left-player-1
  []
  (is (= (-> (create-game)
             (move :p1 :left)
             (get-player-pos :p1))
         {:x 0 :y 0})))

(deftest move-square-come-back-player-1
  []
  (is (= (-> (create-game)
             (move :p1 :right)
             (move :p1 :down)
             (move :p1 :left)
             (move :p1 :up)
             (get-player-pos :p1))
         {:x 0 :y 0})))

(deftest move-right-player-1-cannot-pass-block
  []
  (is (= (-> (create-game)
             (move :p1 :right)
             (move :p1 :right)
             (move :p1 :right)
             (move :p1 :right)
             (get-player-pos :p1))
         {:x 3 :y 0})))

(deftest move-square-come-back-player-2
  []
  (let [setup-state (-> (create-game)
                        (move :p2 :up)
                        (move :p2 :left)
                        (move :p2 :down)
                        (move :p2 :right))
        size (get-size setup-state)]
    (is (= (-> setup-state
               (get-player-pos :p2))
           {:x (dec size) :y (dec size)}))))

(deftest place-bomb-player-1
  []
  (is (= (-> (create-game)
             (place-bomb :p1)
             (get-bomb :p1))
         {:x 0 :y 0})))

(deftest explode-bomb-no-more-bomb
  []
  (is (= (-> (create-game)
             (place-bomb :p2)
             (explode-bomb :p2)
             (get-bomb :p2))
         nil)))

(deftest explode-bomb-no-more-player
  []
  (is (not (-> (create-game)
               (place-bomb :p1)
               (explode-bomb :p1)
               (player-alive? :p1)))))

(deftest player-1-kills-player-2
  []
  (let [excavate (fn [dir1 dir2]
                   (fn [state]
                     (-> state
                         (move :p1 dir1)
                         (move :p1 dir1)
                         (move :p1 dir1)
                         (place-bomb :p1)
                         (move :p1 dir2)
                         (move :p1 dir2)
                         (move :p1 dir2)
                         (explode-bomb :p1)
                         (move :p1 dir1)
                         (move :p1 dir1))))
        excavate-right (excavate :right :left)
        excavate-down (excavate :down :up)
        excavations-8 (fn [excavation-fn] (fn [state] (reduce #(%2 %1) state (repeat 8 excavation-fn))))
        excavations-right (excavations-8 excavate-right)
        excavations-down (excavations-8 excavate-down)]
    (is (not (-> (create-game)
                 (excavations-right)
                 (excavations-down)
                 (move :p1 :down)
                 (move :p1 :down)
                 (move :p1 :right)
                 (move :p1 :right)
                 (place-bomb :p1)
                 (explode-bomb :p1)
                 (player-alive? :p2))))))