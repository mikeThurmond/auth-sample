(ns auth.handler
  (:require
   [auth.middleware :refer [middleware]]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]
   [ring.middleware.params :as params]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.coercion.spec]
   [reitit.ring.coercion :as coercion]
   [reitit.ring :as ring]
   [muuntaja.core :as m]
   [auth.core :as a]
   [clojure.spec.alpha :as s]
   [ring.middleware.cors :refer [wrap-cors]]))

(def mount-target
  [:div#app
   [:h2 "Welcome to auth"]
   [:p "please wait while Figwheel is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")]))


(defn index-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (loading-page)})


;;todo
;;logging - notes
;;data store -notes
;;refactor to other services - notes

;;todo
;;check valid email -> spec docs has example
;;https on message
;;add in spec
;;transit json -> works with my current cljs lib
;;re-read docs

(def app
  (ring/ring-handler
    (ring/router
      [["/" {:get {:handler index-handler}}]
       ["/verify" {:get {:handler index-handler}}]
       ["/forgot" {:get {:handler index-handler}}]
       ["/login" {:post {:parameters {:body {:token string? :password string?}}
                         :handler    (fn [{user :body-params}]
                                       (let [user (a/get-user user)]
                                         (if user
                                           {:status 200}
                                           {:status 401})))}}]
       ["/api"
        ["/forgot-password" {:post
                             {:parameters {:body {:email string?}}
                              :handler    (fn [{email :body-params}]
                                            (a/send-email email)
                                            {:status 200})}}]
        ["/verify-password" {:post
                             {:parameters {:body {:token string? :password string?}}
                              :handler    (fn [{data :body-params}]
                                            (let [response (a/verify-password data)]
                                              (if response
                                                {:status 200}
                                                {:status 401})))}}]]]
      {:data {:coercion   reitit.coercion.spec/coercion
              :muuntaja   m/instance
              :middleware [params/wrap-params
                           muuntaja/format-middleware
                           coercion/coerce-exceptions-middleware
                           coercion/coerce-request-middleware
                           coercion/coerce-response-middleware
                           [wrap-cors :access-control-allow-origin [#".*"]
                            :access-control-allow-methods [:get :put :post :patch :delete]]]}})
    (ring/create-default-handler)))