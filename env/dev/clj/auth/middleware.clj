(ns auth.middleware
  (:require
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.params :refer [wrap-params]]
   [prone.middleware :refer [wrap-exceptions]]
   [ring.middleware.reload :refer [wrap-reload]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.cors :refer [wrap-cors]]))

(def middleware
  [#(wrap-defaults % site-defaults)
   wrap-exceptions
   wrap-reload
   wrap-cors])
