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
                $('#showPostToken').val(response.token); // autofill the token for show-posts
                $('#createPostToken').val(response.token); // autofill the token for create-post
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
                         $('#postsList').empty();  // Clear any previous posts
                         if (posts.length === 0) {
                             // If no posts are available, show a message
                             $('#postsList').append('<p>No posts available.</p>');
                         } else {
                             posts.forEach(function(post) {
                                 const userName= post.userName ? post.userName : 'Unknown User';
                                 let postHtml = `
                                     <div class="post" data-post-id="${post.id}">
                                         <p>${post.content}</p>
                                         <small>Posted by: ${userName} on ${post.createdAt}</small>

                                         <!-- Comments section -->
                                         <div class="comments-list">
                                             <h4>Comments:</h4>
                                             ${post.comments && post.comments.length > 0 ?
                                                 post.comments.map(comment => `
                                                     <div class="comment">
                                                         <p><strong>${comment.user.userName}</strong>: ${comment.comment}</p>
                                                         <small>${comment.createdAt}</small>
                                                     </div>
                                                 `).join('')
                                                 : '<p class="no-comments">No comments yet.</p>'
                                             }
                                         </div>

                                         <!-- Input to add new comment -->
                                         <textarea class="comment-input" placeholder="Write a comment..."></textarea>
                                         <button class="addCommentButton">Add Comment</button>
                                     </div>
                                 `;
                                 $('#postsList').append(postHtml);  // Add post HTML to the list
                             });
                         }
                     },
                    error: function(jqXHR, textStatus, errorThrown) {
                        console.error("Error retrieving posts:", textStatus, errorThrown, jqXHR.responseText);
                        alert("Error retrieving posts.");
                    }
                });
    });

    // Event listener for adding comments
    $(document).on('click', '.addCommentButton', function () {
        const postDiv = $(this).closest('.post'); // Select the post container
        const postId = postDiv.data('post-id'); // Get post ID
        const commentText = postDiv.find('.comment-input').val(); // Get comment text
        const token = $('#showPostToken').val();

        $.ajax({
            url: `/comment/${postId}`,
            type: 'POST',
            headers: { 'Authorization': 'Bearer ' + token },
            contentType: 'application/json',
            data: JSON.stringify({ text: commentText }), // Send comment text as CommentRequestDto
            success: function (commentResponse) {
                // Assuming commentResponse is a CommentResponseDto with user info and created comment
                const newCommentHtml = `<div class="comment">
                                            <p><strong>${commentResponse.user.userName}</strong>: ${commentResponse.comment}</p>
                                            <small>${commentResponse.createdAt}</small>
                                        </div>`;
                const commentList=postDiv.find('.comments-list');
                commentList.find('.no-comments').remove;
                commentList.append(newCommentHtml);
                postDiv.find('.comment-input').val(''); // Clear input
            },
            error: function () {
                alert("Failed to add comment.");
            }
        });
    });

});