(ns game.server
  (:require [game.api]
            [game.core])
  (:require [game.api :refer [translate-game]]
            [game.core :refer [create-game
                               move
                               place-bomb
                               explode-bomb]]
            [org.httpkit.server :refer [run-server]]
            [ring.util.codec :refer [form-decode]]))

(defonce server-atom (atom nil))

(defonce game-state (atom nil))

(defn server-started?
  []
  (boolean (deref server-atom)))

(def remote-addr "http://clojure-game.stjernberg.com")
(def local-addr "http://localhost:8080")

(defn get-headers [request]
  {"Content-Type" "text/html"
   "Access-Control-Allow-Origin" (if (= (get-in request [:headers "origin"])
                                        local-addr)
                                   local-addr
                                   remote-addr)
   "Access-Control-Allow-Credentials" "true"})


(defn handler!
  [request]
  (let [query-string (:query-string request)
        params-map (when query-string (form-decode query-string))
        keyworded-map (into {} (map #(vec (map keyword %)) params-map))
        respond (fn [response-fn]
                  {:status  200
                   :headers (get-headers request)
                   :body    (str (translate-game response-fn))})]
    (cond (= (:uri request) "/create-game")
          (respond (reset! game-state (create-game)))

          (= (:uri request) "/move")
          (respond (swap! game-state move (:player keyworded-map) (:direction keyworded-map)))

          (= (:uri request) "/place-bomb")
          (respond (swap! game-state place-bomb (:player keyworded-map)))
          (= (:uri request) "/explode-bomb")
          (respond (swap! game-state explode-bomb (:player keyworded-map)))
          :else
          (respond (deref game-state)))))

(defn start-server!
  []
  (if (server-started?)
    "The server is already started!"
    ((reset! game-state (create-game))
     (reset! server-atom
             (run-server handler! {:port 8001})))))

(defn stop-server!
  []
  (if-not (server-started?)
    "The server is not started!"
    (let [stop-server-fn (deref server-atom)]
      (stop-server-fn :timeout 100)
      (reset! server-atom nil))))

(defn restart-server!
  []
  (stop-server!)
  (start-server!))

(comment
  (start-server!)
  (server-started?)
  (restart-server!)
  (stop-server!))
