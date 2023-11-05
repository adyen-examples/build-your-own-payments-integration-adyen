const clientKey = document.getElementById("clientKey").innerHTML;

async function startGiving() {

  //TODO : Instantiate the checkout object, as well as the donationConfig object. Use handleDonation as the submit action handler.

  try {
    checkout.create('donation', donationConfig).mount('#donation-container');
  } catch (ex) {
    console.warn(ex);
  }
}

async function handleDonation(amount) {
  try {
    console.log(amount);
    // TODO : Send donated amount to backend

    switch (res.status) {
      case "completed":
        window.location.href = "/result/donated";
        break;
      default:
        window.location.href = "/result/error";
        break;
    }
  } catch (error) {
    console.error(error);
    alert("Error occurred. Look at console for details");
  }
}

async function sendPostRequest(url, data) {
  const res = await fetch(url, {
    method: "POST",
    body: data ? JSON.stringify(data) : "",
    headers: {
      "Content-Type": "application/json",
    },
  });

  return await res.json();
}

startGiving();
