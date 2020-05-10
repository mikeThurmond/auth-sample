(ns auth.routes
  (:require
    [reitit.frontend :as reitit]))


(def router
  (reitit/router
    [["/" :index]
     ["/forgot" :forgot]
     ["/verify" :reset]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))
