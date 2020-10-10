(ns movies.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [movies.events :as events]
   [movies.routes :as routes]
   [movies.views :as views]
   [movies.config :as config]
   ))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (routes/app-routes)
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::events/load-images-config])
  (dev-setup)
  (mount-root))
