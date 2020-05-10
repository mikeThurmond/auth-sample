(ns auth.services
  (:require
    [ajax.core :refer [GET POST]]))


(def cors "https://cors-anywhere.herokuapp.com/")
(def base-url "https://localhost:3000")
(def success-message "success message")
(def error-message "something went wrong")

(defn forgot-password [form message]
  (POST (str "localhost:3000/api/forgot-password")
    {:body          @form
     :handler       #(reset! message success-message)
     :error-handler (fn [error]
                      (reset! message error-message))}))


(defn reset-password [form message]
  (POST "localhost:3000/api/verify-password"
    {:body          @form
     :handler       #(reset! message success-message)
     :error-handler (fn [error]
                      (reset! message error-message))}))
