$ ->
  ws = new WebSocket $("body").data("ws-url")
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.type
      when "message"
        addJsonMessage(message)
      else
        console.log(message)


addJsonMessage = (message) ->
    addMessageToTree(message)
    console.log(message)