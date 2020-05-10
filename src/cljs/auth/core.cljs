(ns auth.core
  (:require
    [auth.routes :as routes]
    [auth.views :as views]
    [reagent.core :as reagent :refer [atom]]
    [reagent.dom :as rdom]
    [reagent.session :as session]
    [reitit.frontend :as reitit]
    [clerk.core :as clerk]
    [accountant.core :as accountant]))


;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'views/home-page
    :forgot #'views/forgot-page
    :reset #'views/reset-page))

;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [page]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path routes/router path)
            current-page (:name (:data  match))
            route-params (:path-params match)
            query-params (:query-params match)]
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params
                              :query-params query-params})
        (clerk/navigate-page! path)))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path routes/router path)))})
  (accountant/dispatch-current!)
  (mount-root))
