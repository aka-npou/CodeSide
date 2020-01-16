package strategy;

import model.*;
import myModel.Dodge;
import myModel.Shoot;
import myModel.UnitStatus;
import myModel.World;
import util.GenShootChain;

/**
 * Created by aka_npou on 30.11.2019.
 */
public abstract class AkaNpouStrategy {

    int currentTick=-1;

    World world = new World();

    Dodge dodge = new Dodge();
    Shoot shoot = new Shoot();
    GenShootChain genShootChain = new GenShootChain();

    UnitStatus unitStatus = UnitStatus.Wait;

    Unit nearestEnemy = null;

    public abstract UnitAction getAction(Unit unit, Game game, Debug debug);
}
