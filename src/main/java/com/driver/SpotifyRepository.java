package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user=new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist=new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist=null;
        for(Artist art:artists){
            if(artistName.equals(art.getName())){
                artist=art;
                break;
            }
        }
        Album album=new Album(title);
        albums.add(album);
        if(artist==null){
            artist=new Artist(artistName);
            artists.add(artist);
        }
        if(!artistAlbumMap.containsKey(artist)){
            List<Album> al=new ArrayList<>();
            al.add(album);
            artistAlbumMap.put(artist,al);
        }else{
            List<Album> al=artistAlbumMap.get(artist);
            al.add(album);
        }
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{

        Album alb=null;
        for(Album album:albums){
            if(album.getTitle().equals(albumName)){
                alb=album;
                break;
            }
        }
        if(alb==null) throw new Exception("Album does not exist");
        Song song=new Song(title,length);
        songs.add(song);
        if(!albumSongMap.containsKey(alb)){
            List<Song>lis=new ArrayList<>();
            lis.add(song);
            albumSongMap.put(alb,lis);
        }else{
            List<Song>lis=albumSongMap.get(alb);
            lis.add(song);
//            albumSongMap.put(alb,lis);
        }
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user=null;
        for(User us:users){
            if(us.getMobile().equals(mobile)){
                user=us;
                break;
            }
        }
        if(user==null) throw new Exception("User does not exist");
        Playlist playlist=new Playlist(title);
        List<Song>lis=new ArrayList<>();
        for(Song song:songs){
            if(song.getLength()==length){
                lis.add(song);
                break;
            }
        }
        playlistSongMap.put(playlist,lis);
        playlists.add(playlist);

        if(!userPlaylistMap.containsKey(user)){
            List<Playlist>play=new ArrayList<>();
            play.add(playlist);
            userPlaylistMap.put(user,play);
        }else{
            List<Playlist>play=userPlaylistMap.get(user);
            play.add(playlist);
//            userPlaylistMap.put(user,play);
        }
        creatorPlaylistMap.put(user,playlist);
        List<User>usrlist=new ArrayList<>();
        usrlist.add(user);
        playlistListenerMap.put(playlist,usrlist);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user=null;
        for(User us:users){
            if(us.getMobile().equals(mobile)){
                user=us;
                break;
            }
        }
        if(user==null) throw new Exception("User does not exist");
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);
        List<Song>lis=new ArrayList<>();
        for(String str:songTitles){
            for(Song song:songs){
                if(str.equals(song.getTitle())){
                    lis.add(song);
                    break;
                }
            }
        }
        playlistSongMap.put(playlist,lis);

        creatorPlaylistMap.put(user,playlist);
        List<User>userlist=new ArrayList<>();
        userlist.add(user);
        playlistListenerMap.put(playlist,userlist);
        if(!userPlaylistMap.containsKey(user)){
            List<Playlist>play=new ArrayList<>();
            play.add(playlist);
            userPlaylistMap.put(user,play);
        }else{
            List<Playlist>play=userPlaylistMap.get(user);
            play.add(playlist);
//            userPlaylistMap.put(user,play);
        }
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user=null;
        for(User us:users){
            if(us.getMobile().equals(mobile)){
                user=us;
                break;
            }
        }
        if(user==null) throw new Exception("User does not exist");
        Playlist playlist=null;
        for(Playlist play:playlists){
            if(play.getTitle().equals(playlistTitle)){
                playlist=play;
                break;
            }
        }

        if(playlist==null) throw new Exception("Playlist does not exist");
        if(creatorPlaylistMap.containsKey(user))
            return playlist;
        List<User>listener=playlistListenerMap.get(playlist);
        for(User us:listener){
            if(us.getMobile().equals(user.getMobile())){
                return playlist;
            }
        }
        listener.add(user);
        playlistListenerMap.put(playlist,listener);
        if(!userPlaylistMap.containsKey(user)){
            List<Playlist>pl=new ArrayList<>();
            pl.add(playlist);
            userPlaylistMap.put(user,pl);
        }else{
            List<Playlist>pl=userPlaylistMap.get(user);
            pl.add(playlist);
        }
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user=null;
        Song song=null;
        for(User us:users){
            if(us.getMobile().equals(mobile)){
                user=us;
                break;
            }
        }
        if(user==null) throw new Exception("User does not exist");
        for(Song so:songs){
            if(so.getTitle().equals(songTitle)){
                song=so;
                break;

            }
        }
        if(song==null) throw new Exception("Song does not exist");
        if(!songLikeMap.containsKey(song)){
            List<User>userl=new ArrayList<>();
            userl.add(user);
            songLikeMap.put(song,userl);
        }else {
            for (User use : songLikeMap.get(song)) {
                if (use.getMobile().equals(user.getMobile())) {
                    return song;
                }
            }
            songLikeMap.get(song).add(user);
        }
        song.setLikes(song.getLikes()+1);
        for(Artist art:artistAlbumMap.keySet()){
            for(Album alb:artistAlbumMap.get(art)){
                if(albumSongMap.containsKey(alb)){
                    for(Song son:albumSongMap.get(alb)){
                        if(son.getTitle().equals(songTitle)){
                            art.setLikes(art.getLikes()+1);
//                            artists.add(art);
                            return song;
                        }
                    }
                }
            }
        }
        return song;
    }

    public String mostPopularArtist() {
        int like=0; Artist artist=null;
        for (Artist art:artists){
            if(art.getLikes()>like){
                like=art.getLikes();
                artist=art;
            }
        }
        return artist!=null?artist.getName():null;
    }

    public String mostPopularSong() {
        int like=0; Song song=null;
        for(Song key:songs){
            if(key.getLikes()>like){
                like= key.getLikes();
                song=key;
            }
        }
        return song!=null?song.getTitle():null;
    }

}
