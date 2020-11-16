package jminusminus;

public class JDoStatement extends JStatement {

    private JStatement body;
    private JExpression condition;

    JDoStatement (int line, JStatement statement, JExpression expression) {
        super(line);
        this.body = statement;
        this.condition = expression;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        condition = condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        body = (JStatement) body.analyze(context);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        String bodyLabel = output.createLabel();
        output.addLabel(bodyLabel);
        body.codegen(output);

        condition.codegen(output, bodyLabel, true);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JDoStatement:" + line, e);

        JSONElement bodyJson = new JSONElement();
        e.addChild("Body", bodyJson);
        body.toJSON(bodyJson);

        JSONElement conditionJson = new JSONElement();
        e.addChild("Condition", conditionJson);
        condition.toJSON(conditionJson);
    }

}
