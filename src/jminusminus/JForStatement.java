package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;

public class JForStatement extends JStatement {

    private ArrayList<JStatement> forInit;
    private JExpression condition;
    private ArrayList<JStatement> update;
    private JStatement body;
    private LocalContext localContext;

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
        // Create a new LocalContext with context as the parent
        this.localContext = new LocalContext(context);

        // Analyze the init in the new context
        ArrayList<JStatement> newForInit = new ArrayList<>();
        for (JStatement init : forInit) {
            init = (JStatement) init.analyze(localContext);
            newForInit.add(init);
        }
        this.forInit = newForInit;

        // Analyze the condition in the new context and make sure it’s a boolean
        condition = condition.analyze(localContext);

        // Analyze the update in the new context
        ArrayList<JStatement> newForUpdate = new ArrayList<>();
        for (JStatement u : update) {
            u = (JStatement) u.analyze(localContext);
            newForUpdate.add(u);
        }
        this.update = newForUpdate;

        // Analyze the body in the new context
        body = (JStatement) body.analyze(localContext);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // Start and exit labels to handle looping and exiting
        String conditionLabel = output.createLabel();
        breakLabel = output.createLabel();

        // Generate code for the init statements
        for (JStatement init : forInit) {
            init.codegen(output);
        }

        // Add the start label and check the condition -- jump to exit when false
        output.addLabel(conditionLabel);
        condition.codegen(output, breakLabel, false);

        // Generate the code for the body
        body.codegen(output);

        // Generate the code for the update
        for (JStatement u : update) {
            u.codegen(output);
        }

        // Jump back to the condition
        output.addBranchInstruction(GOTO, conditionLabel);

        // Add the exit label for when condition is false
        output.addLabel(breakLabel);
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
