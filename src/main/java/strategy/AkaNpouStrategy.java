package strategy;

import model.Debug;
import model.Game;
import model.Unit;
import model.UnitAction;
import myModel.Dodge;
import myModel.Shoot;
import myModel.UnitStatus;
import myModel.World;
import util.GenShootChain;

/**
 * Created by aka_npou on 30.11.2019.
 */
public abstract class AkaNpouStrategy {

    World world = new World();

    Dodge dodge = new Dodge();
    Shoot shoot = new Shoot();
    GenShootChain genShootChain = new GenShootChain();

    UnitStatus unitStatus = UnitStatus.Wait;

    public abstract UnitAction getAction(Unit unit, Game game, Debug debug);
}
