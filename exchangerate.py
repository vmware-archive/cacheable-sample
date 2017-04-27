import os
import os.path
import subprocess
import sys

locator_host = 'localhost'
locator_port = 10000
prefix = 'qa_'

if __name__ == '__main__':
   if 'JAVA_HOME' not in os.environ:
      sys.exit('Please set the JAVA_HOME environment variable')

   if len(sys.argv) < 3:
      sys.exit('Please provide a from and to currency (e.g. exchangerate.py EUR USD)')

   here = os.path.abspath(os.path.dirname(sys.argv[0]))
   java = os.path.join(os.environ['JAVA_HOME'],'bin','java')

   classpath=os.pathsep.join([os.path.join(here,'target','*'),os.path.join(here,'target','dependency','*')])

   className ='io.pivotal.pde.sample.cacheable.ExchangeRate'

   dashDees = ['-Dgemfire.locator.host={0}'.format(locator_host),'-Dgemfire.locator.port={0}'.format(locator_port), '-Dgemfire.cache.prefix={0}'.format(prefix)]

   cmd = [java, '-cp', classpath] + dashDees +  [className ] + sys.argv[1:]

   print('running command: ' + ' '.join(cmd))

   subprocess.check_call(cmd)
