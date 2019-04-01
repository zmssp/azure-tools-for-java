# Deployment slot support in eclipse toolkit
Deployment slots are live apps with their own hostnames. App content and configurations elements can be swapped between two deployment slots, including the production slot.
For more details about slot, please refer to [slot](https://docs.microsoft.com/en-us/azure/app-service/deploy-staging-slots).
## Deploy app service to slot
You can choose one of the following options, select an existing slot or create a new one.

![Deploy App Service Dialog][create-app-service-dialog]

## Configure slot using azure explorer
You can manage your slot by right-clicking on it and selecting one of the options on the context menu. For example, you can Start, Stop, or Delete your slot.

![explorer-slot][explorer-slot]


And you can click show properties, in the pop up dialog, you can configure the app settings.

![slot-settings][slot-settings]


[create-app-service-dialog]: deploy-slot.png
[explorer-slot]: explorer-slot.png
[slot-settings]: slot-setting.png