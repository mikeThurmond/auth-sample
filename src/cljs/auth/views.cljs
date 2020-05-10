(ns auth.views
  (:require
    [auth.routes :as routes]
    [auth.services :as services]
    [reagent.core :refer [atom]]
    [reagent.session :as session]))

(defn input-element [placeholder value]
  [:input {:class          "form-control"
           :type           "text"
           :placeholder    placeholder
           :value          @value
           :on-change      #(reset! value (-> % .-target .-value))}])

(defn input-form-element [placeholder form keyword]
  [:input {:class          "form-control"
           :type           "text"
           :placeholder    placeholder
           :on-change      #(swap! form assoc keyword %)}])

(defn home-page []
  (let [user (atom nil)
        password (atom nil)]
    (fn []
      [:span.main
       [:h1 "Sign In"]
       [:form
        [input-element "user" user]
        [:br]
        [input-element "password" password]
        [:input
         {:type  "button"
          :value "Login"
          ; :on-click
          }]
        [:br][:br]
        [:a {:href (routes/path-for :forgot)} "Forgot Password?"]]])))


(defn forgot-page []
  (let [email (atom nil)
        message (atom nil)
        form (atom {})]
    (fn []
      [:span.main
       [:h1 "Forgot Password"]
       [:form
        {:on-submit (fn [e]
                      (.preventDefault e)
                      (when (:email @form)
                        (services/forgot-password form message)))}
        [input-form-element "Email" form :email]
        [:br]
        [:button
         {:type "submit"}
         "Reset Password"]
        [:br][:br]]])))


(defn reset-page []
  (let [routing-data (session/get :route)
        token (get-in routing-data [:query-params :token])
        password (atom nil)
        confirmed-pw (atom nil)]
    (fn []
      [:span.main
       [:h1 "Reset Password"]
       [:form
        [input-element "Password" password]
        [:br]
        [input-element "Confirm Password" confirmed-pw]
        [:br]
        [:input
         {:type  "button"
          :value "Login"
          ; :on-click
          }]
        [:br][:br]]])))
