# InstaCrawl

This project is a Kotlin-based API crawler for Instagram. 

## Features

It can be used to crawl for a given user:
- Profile
- Followers
- Followings
- Posts
- Likes of a Post

## API

It makes use of the public profile API (`https://instagram.com/{username}/?__a=1`) and the Graph API (`https://www.instagram.com/graphql`).
The Graph API has rate limits in place, which are handled by the crawler.

## Authorization

Some information is only available with an authorization (e.g. private profiles and some Graph API calls).
The crawler can use the string from the Cookie header field which is sent by the browser with every request made in the Instagram Web App.
They can be specified in the `resources/apikeys.json`.

## `UserStore`

The crawler aggregates and serializes the queried information to a `UserStore`, which is essentially a user dictionary including references to other user objects.

## Samples

`SampleCrawler.kt` is a sample crawler that crawls users recursively by their followers and followings.

`SampleAnalytics.kt` is a sample using the `UserStore` to find follower cliques, search for certain posts and downloads all images of a given user.
It makes use of the [JGraphT](https://jgrapht.org/) library.
