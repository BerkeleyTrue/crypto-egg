(ns chalk.core
  (:require
    ["chalk" :as chalkjs])
  (:require-macros
    [chalk.macro :refer [gen-methods]]))

(gen-methods
  chalkjs
  :reset :bold :dim :italic :underline :inverse :hidden :strikethrough
  :black :red :green :yellow :blue :magenta :cyan :white :gray :grey
  :black-bright :red-bright :green-bright :yellow-bright :blue-bright :magenta-bright :cyan-bright :white-bright
  :bg-black :bg-red :bg-green :bg-yellow :bg-blue :bg-magenta :bg-cyan :bg-white :bg-gray
  :bg-black-bright :bg-red-bright :bg-green-bright :bg-yellow-bright :bg-blue-bright :bg-magenta-bright :bg-cyan-bright :bg-white-bright)
