$(document).ready(function() {
    // Register functionality
    $('#registerButton').on('click', function(){
        var username = $('#registerUsername').val(); // Get the username
        var password = $('#registerPassword').val(); // Get the password

        if (!username || !password) {
            alert("Please fill in all fields.");
            return; // Stop if any field is empty
        }

        $.ajax({
            url: '/register', // Your registration endpoint
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ userName: username, passWord: password }),
            success: function(response) {
                alert(response); // Alert registration success message
            },
            error: function() {
                alert("Error registering user.");
            }
        });
    });

    // Login functionality
    $('#loginButton').on('click', function() {
        var username = $('#loginUsername').val(); // Get the username
        var password = $('#loginPassword').val(); // Get the password

        if (!username || !password) {
            alert("Please fill in all fields.");
            return; // Stop if any field is empty
        }

        $.ajax({
            url: '/login', // Your login endpoint
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ userName: username, passWord: password }),
            success: function(response) {
                // Assuming the response contains the token
                $('#showPostToken').val(response.token); // autofill the token
                $('#createPostToken').val(response.token); // autofill the token
                alert("Login successful! Token: " + response.token); // Show success message
            },
            error: function() {
                alert("Invalid username or password.");
            }
        });
    });

    // Create Post functionality
    $('#createPostButton').on('click', function() {
        var token = $('#createPostToken').val(); // Get the saved token
        var content = $('#postContent').val(); // Get the post content

        if (!postContent) {
            alert("Please write something before posting.");
            return; // Stop if the post content is empty
        }

        $.ajax({
            url: '/posts', // Your create post endpoint
            method: 'POST',
            contentType: 'application/json',
            headers: {
                'Authorization': 'Bearer ' + token // Add token to the headers
            },
            data: JSON.stringify({ content: content }), // Assuming your DTO for posts has a 'content' field
            success: function(response) {
                alert(response.message); // Show success message
                $('#postContent').val(''); // Clear the textarea after posting
            },
            error: function(xhr) {
                alert("Error creating post: " + (xhr.responseJSON?.error || "Unknown error")); // Show error message
                        }
        });
    });

    // View Posts functionality
    $('#viewPostsButton').on('click', function() {
        var token = $('#showPostToken').val(); // Retrieve the saved token

        $.ajax({
            url: '/posts', // Endpoint for getting posts
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token // Attach token in the headers
            },
             success: function(posts) {
                        console.log("Posts received:", posts); // Inspect the structure
                        $('#postsList').empty();
                        posts.forEach(function(post) {
                            $('#postsList').append(
                                `<div class="post">
                                    <p>${post.content}</p>
                                    <small>${post.createdAt}</small>
                                 </div>`
                            );
                        });
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        console.error("Error retrieving posts:", textStatus, errorThrown, jqXHR.responseText);
                        alert("Error retrieving posts.");
                    }
                });
    });
});