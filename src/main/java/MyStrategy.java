import model.*;
import myModel.Constants;
import strategy.AkaNpouStrategy;
import strategy.*;


//cd "E:\java\mail cups\ai\big\2019\aicup2019-windows"
// .\aicup2019.exe --config "E:\java\mail cups\ai\big\2019\aicup2019-windows\config no spread.json"

public class MyStrategy {

    AkaNpouStrategy akaNpouStrategy = new Strategy_v0();

    public UnitAction getAction(Unit unit, Game game, Debug debug) {

        UnitAction action = akaNpouStrategy.getAction(unit, game, debug);

        if (Constants.ON_DEBUG) {
            debug.draw(new CustomData.Log("v="+action.velocity+
                                               " j="+action.jump+
                                               " jd="+action.jumpDown+
                                               " r="+action.reload+
                                               " s="+action.shoot+
                                               " a="+action.aim.x+"/"+action.aim.y+
                                               " w="+action.swapWeapon+
                                               " m="+action.plantMine));
            if (unit.weapon!=null)
                debug.draw(new CustomData.Log("sp="+unit.weapon.spread+" la="+unit.weapon.lastAngle+" mb="+unit.weapon.magazine));
        }
        return action;
    }

}