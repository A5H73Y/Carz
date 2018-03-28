package me.A5H73Y.Carz.controllers.race;

public class Race {
    /*

    private List<Racer> racers = new ArrayList<>();
    private List<Location> checkpoints = new ArrayList<>();

    private String trackName;
    private Location startLocation;
    private int laps;
    private int minimumPlayers;
    private int maximumPlayers;
    private boolean started;

    private long startTime;

    private Race(){}

    public Race(String trackName) {
        this.trackName = trackName;
        this.startLocation = getTrackStartLocation(trackName);
        this.laps = carz.getConfig().getInt("Track." + trackName + ".Laps", 3);
        this.minimumPlayers = carz.getConfig().getInt("Track." + trackName + ".MinimumPlayers", 4);
        this.maximumPlayers = carz.getConfig().getInt("Track." + trackName + ".MaximumPlayers", 8);
    }

    public void playerJoin(Player player) {
        player.sendMessage("You joined a race");
        racers.add(new Racer(this, player));
        teleportToStart(player);
    }

    public void playerLeave(Player player) {
        racers.remove(player);
    }

    public void teleportToStart(Player player) {
        player.teleport(startLocation);
    }

    public String getTrackName() {
        return trackName;
    }

    public void beginCountdown() {
        int count = 5;

        while (count > 1) {
            for (Racer racer : racers) {
                racer.getPlayer().sendMessage("Race starting in " + count + " seconds...");
            }
            count--;
        }

        startRace();
    }

    private void startRace() {
        started = true;
        startTime = System.currentTimeMillis();
    }

    private double getTimeElapsed() {
        return System.currentTimeMillis() - startTime;
    }

    public boolean hasStarted() {
        return started;
    }

    public boolean isFull() {
        return racers.size() == maximumPlayers;
    }

    public boolean hasEnoughPlayersToStart() {
        return racers.size() >= minimumPlayers;
    }

    public int getNumberOfLaps() {
        return laps;
    }

    if (materialBelow == Material.SPONGE && finishedLanding) {
        playerVelocity.setY(0.5D);
        playerVelocity.setX(player.getEyeLocation().getDirection().getX() + 500);
        playerVelocity.setZ(player.getEyeLocation().getDirection().getZ() + 500);
        minecart.setMetadata("finished_landing", new MetaInfo(carz, false));
    }

    boolean finishedLanding = true;

    if (minecart.hasMetadata("finished_landing")) {
        finishedLanding = minecart.getMetadata("finished_landing").get(0).asBoolean();
    }
    if (materialBelow != Material.AIR && !finishedLanding)
            minecart.removeMetadata("finished_landing", carz);

    */
}
