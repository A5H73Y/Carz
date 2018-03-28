package me.A5H73Y.Carz.controllers.race;

public class RaceController {
    /*

    private final Carz carz;
    Set<Race> races = new HashSet<>();

    private final static String RACING_KEY = "RACING";

    public RaceController(Carz carz) {
        this.carz = carz;
    }

    public boolean isInRace(Player player) {
        return player.hasMetadata(RACING_KEY);
    }

    public void joinRandomTrack(Player player) {
        Race race = getAnyAvailableRace();
        race.playerJoin(player);
    }

    public boolean joinTrack(Player player, String trackName) {
        Race race = getAvailableRace(trackName);

        if (race.isFull()) {
            player.sendMessage("Race is full");
            return false;
            
        } else if (race.hasStarted()) {
            player.sendMessage("Race has already started!");
            return false;
        }
        
        race.playerJoin(player);
        return true;
    }

    public Race getAvailableRace(String trackName) {
        for (Race race : races) {
            if (race.getTrackName().equals(trackName))
                return race;
        }

        Race race = new Race(trackName);
        races.add(race);
        
        return race;
    }

    public Race getAnyAvailableRace() {
        for (Race race : races) {
            if (!race.hasStarted() && !race.isFull())
                return race;
        }

        Race race = new Race(getRandomTrackName());
        races.add(race);

        return race;
    }

    public Set<String> getAllTracks() {
        return carz.getConfig().getConfigurationSection("Tracks").getKeys(false);
    }

    private String getRandomTrackName() {
        return Utils.getRandomString(new ArrayList<>(getAllTracks()));
    }

    public void createNewTrack(Player player, String trackName) {
        if (getAllTracks().contains(trackName)) {
            player.sendMessage("This track already exists!");
            return;
        }

        carz.getConfig().set("Track." + trackName + ".World", player.getLocation().getWorld().getName());
        carz.getConfig().set("Track." + trackName + ".X", player.getLocation().getX());
        carz.getConfig().set("Track." + trackName + ".Y", player.getLocation().getY());
        carz.getConfig().set("Track." + trackName + ".Z", player.getLocation().getZ());
        carz.getConfig().set("Track." + trackName + ".Yaw", player.getLocation().getYaw());
        carz.getConfig().set("Track." + trackName + ".Pitch", player.getLocation().getPitch());
        carz.saveConfig();
        player.sendMessage("track " + ChatColor.AQUA + trackName + " created!");
    }

    public Location getTrackStartLocation(String trackName) {
        World world = Bukkit.getWorld(carz.getConfig().getString("Track." + trackName + ".World"));
        double x = carz.getConfig().getDouble("Track." + trackName + ".X");
        double y = carz.getConfig().getDouble("Track." + trackName + ".Y");
        double z = carz.getConfig().getDouble("Track." + trackName + ".Z");
        float yaw = carz.getConfig().getLong("Track." + trackName + ".Yaw");
        float pitch = carz.getConfig().getLong("Track." + trackName + ".Pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public void finishRace(Race race) {
        races.remove(race);
    }

    public void leaveRace(Player player) {
        player.removeMetadata(RACING_KEY, carz);
    }
    */
}
