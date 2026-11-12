from pathlib import Path
import json
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
projects = ['api-gateway', 'ms-catalogo', 'ms-ordenes', 'ms-carrito', 'ms-notificaciones']
assert not (root / 'docker-compose.yml').exists()
assert not list(root.rglob('Dockerfile'))
assert not list(root.rglob('.dockerignore'))
for name in projects:
    project = root / 'backend' / name
    ET.parse(project / 'pom.xml')
    assert (project / 'src/main/resources/application.yml').exists()
    assert (project / 'README.md').exists()
json.loads((root / 'frontend/package.json').read_text(encoding='utf-8'))
rabbit = json.loads((root / 'infrastructure/rabbitmq/definitions.json').read_text(encoding='utf-8'))
assert len(rabbit['bindings']) == 5
print('Verificacion estatica local: OK')
