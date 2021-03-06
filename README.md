# meetup-rsvp

Never miss a meetup again! :-D

This application simply queries the Meetup API for upcoming events of the groups you specify, and RSVPs if you haven't already.

## Usage

Get your API key from https://secure.meetup.com/meetup_api/key

Store it somewhere safe...

```bash
# Keep it safe!
export MEETUP_API_KEY=secret
```

Then...

```bash
./mvnw package

java -jar <jar-file> SomePopularMeetup [ OtherAmazingMeetup ... ]
```

## Options

The following environment variables can be set:

##### MEETUP_WAITLIST_ONLY

Set this variable to true if you only want to RSVP if this places you in the waitlist (i.e. yesRsvpCount >= rsvpLimit)

## Finally

Please do respect others and don't abuse the API!

