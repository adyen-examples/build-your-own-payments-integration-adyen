const clientKey = document.getElementById("clientKey").innerHTML;

// Helper function that sends a POST request to your backend
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

// Handles donation result
async function handleDonation(amount) {
  try {
    console.log(amount);

    // TODO: call your server "/api/donations end point and handle the response
    const res = await sendPostRequest(`/api/donations`, amount);

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

// Instantiates the configuration & mounts the donation component
async function startGiving() {
  const checkout= await AdyenCheckout({
    clientKey,
    environment: "test",
  });

  // TODO: Instantiate the donationConfiguration
  const donationConfig = {
    /* Add configuration here */
    onDonate: (state, component) => {
      if(state.isValid) {
        console.log("Initiating donation");
        handleDonation(state.data.amount);
      }
    },
    onCancel: (result, component) => {
      console.log("Donation cancelled");
      console.log(result);
      document.getElementById('donation-container').style.display = 'none';
    }
  };

  try {
    checkout.create('donation', donationConfig).mount('#donation-container');
  } catch (ex) {
    console.warn(ex);
  }
}

startGiving();