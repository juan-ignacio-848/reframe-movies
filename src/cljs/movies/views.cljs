(ns movies.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core :as re-com]
   [movies.subs :as subs]
   ))

(defn home-panel []
  [:div
   "Hello world"])

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [re-com/v-box
     :height "100%"
     :children [[panels @active-panel]]]))
