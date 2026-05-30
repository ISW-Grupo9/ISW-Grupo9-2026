import { useState, useEffect } from 'react';

export interface ParqueReglas {
  diasCerrados: string[]; // e.g. ["MONDAY"]
  feriadosFijos: string[]; // e.g. ["12-25", "01-01"]
}

const DEFAULT: ParqueReglas = { diasCerrados: [], feriadosFijos: [] };

export function useParqueReglas(): ParqueReglas {
  const [reglas, setReglas] = useState<ParqueReglas>(DEFAULT);

  useEffect(() => {
    fetch('/api/parque/reglas')
      .then((r) => r.json())
      .then(setReglas)
      .catch(() => {});
  }, []);

  return reglas;
}
