The Basics
======

## Permissions

If you want to use permissions for the plugin, ensure `Other.UsePermissions` in the `config.yml` is set to `true`. Otherwise, the players will not require permissions for most functionality.

| Permission Node | Description |
|-|-|
| **Carz.\*** | **All Carz permissions.** |
| **Carz.Admin** | **Ability to perform Administration commands.** |
| **Carz.Purchase** | **Ability to purchase an owned Car.** |
| **Carz.Place** | **Ability to place a Car.** |
| **Carz.Drive** | **Ability to drive a Car.** |
| **Carz.Upgrade** | **Ability to upgrade a Car.** |
| **Carz.CreateSign** | **Ability to create Carz signs.** |
| **Carz.BypassOwner** | **Bypass the ownership checks of the Car.** |

## Carz Commands

Every available command can be found in-game using `/carz cmds`. The list will be built based on permissions and configuration.  
_For console commands, use `/carzc cmds`._

![Carz Commands Summary](https://i.imgur.com/9RhUea2.png "Carz Commands Summary")

<script>
  fetch('files/carzCommands.json')
    .then(function(response) {
      return response.json();
    })
    .then(function(data) {
      appendData(data);
    })
    .catch(function(err) {
      console.log(err);
    });
    
    function appendData(data) {
      data = data.reverse();
      let mainContainer = document.getElementById("carz-commands");

      for (let i = 0; i < data.length; i++) {
        mainContainer.insertAdjacentHTML('afterend', createCommandSummary(data[i]));
      }
    }
    
    function createCommandSummary(command) {
        return `<details>
                <summary>${command.command} - ${command.title}</summary>
                <div>
                    <p>Syntax: <code>/carz ${command.command} ${command.arguments || ''}</code></p>
                    <p>Example: <code>${command.example}</code></p>
                    <p>Permission: <code>${command.permission || 'None required'}</code></p>
                    <p>Console Command: <code>${command.consoleSyntax || 'N/A'}</code></p>
                    <p>Description: ${command.description}</p>
                    <p>${command.imagePath ? `<img src="${command.imagePath}" alt="${command.command}"/>` : ''}</p>
                </div>
            </details>`;
    }
</script>
