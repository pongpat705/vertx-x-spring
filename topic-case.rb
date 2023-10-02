@startuml

entity producer
queue kafka
entity grillconsumer
entity drinkconsumer

alt grill case
producer --> kafka : produce topic process.grill
grillconsumer -> kafka : consume topic process.grill
grillconsumer -> grillconsumer : prep material
grillconsumer -> grillconsumer : grill
grillconsumer -> grillconsumer : prep dish
end

alt drink case
producer --> kafka : produce topic process.drink
drinkconsumer -> kafka : consume topic process.drink
drinkconsumer -> drinkconsumer : prep drink
end

@enduml