package jminusminus;

import java.util.ArrayList;

public class JForStatement extends JStatement {

    private ArrayList<JStatement> forInit;
    private JExpression condition;
    private ArrayList<JStatement> update;
    private JStatement body;

    JForStatement (int line, ArrayList<JStatement> forInit, JExpression condition, ArrayList<JStatement> update, JStatement body) {
        super(line);
        this.forInit = forInit;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }


    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {

    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JForStatement:" + line, e);

        JSONElement init = new JSONElement();
        e.addChild("Init", init);
        forInit.forEach(x -> x.toJSON(init));

        JSONElement cond = new JSONElement();
        e.addChild("Condition", cond);
        condition.toJSON(cond);

        JSONElement upd = new JSONElement();
        e.addChild("Update", upd);
        update.forEach(x -> x.toJSON(upd));

        JSONElement bod = new JSONElement();
        e.addChild("Body", bod);
        body.toJSON(bod);
    }

}
