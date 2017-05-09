class Stage implements Serializable {
    String name

    Stage(java.util.LinkedHashMap opts) {
        name = opts.name
    }
}

class BStage extends Stage {
    String bp

    BStage(java.util.LinkedHashMap opts) {
        super(opts)
        bp = opts.bp
    }

    def call1(env) {
        env.stage('build') {
            env.node('master') {
                env.sh 'echo build - ' + bp
            }
        }
    }
}

class DStage extends Stage {
    String dp

    DStage(java.util.LinkedHashMap opts) {
        super(opts)
        dp = opts.dp
    }

    def call1(env) {
        env.stage('deploy - ' + dp) {
            env.node('master') {
                env.sh 'echo deploy - ' + dp
            }
        }
    }
}

class GStage extends Stage {
    Closure runner
    LinkedHashMap opts

    DStage(java.util.LinkedHashMap opts) {
        super(opts)
        runner = opts.runner
        this.opts = opts
    }

    def call1(env) {
        runner.call(env, opts)
    }
}

class Stages {

    def static stage(opts) {
        if(opts.type == 'b') {
            return new BStage(opts.options)
        } else if(opts.type == 'd') {
            return new DStage(opts.options)
        } else if(opts.type == 'g') {
            return new GStage(opts.options)
        }
    }

}


def constructPipeline(opts, java.util.LinkedHashMap defopts=[]) {
    opts.putAll(defopts)
    for(stage in opts.stages) {
        Stages.stage(stage).call1(this)
    }
}

return this

