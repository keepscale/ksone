import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ErrorService } from '../../error/error.service';

@Injectable({
  providedIn: 'root'
})
export class VersionCheckService {
  // this will be replaced by actual hash post-build.js
  private currentHash = '{{POST_BUILD_ENTERS_HASH_HERE}}';
  constructor(
    private http: HttpClient,
    private error: ErrorService) { }
  
  public initVersionCheck(frequency = 1000 * 60 * 30) {
    setInterval(() => {
      this.checkVersion();
    }, frequency);
    this.checkVersion();
  }
  /**
  * Will do the call and check if the hash has changed or not
  * @param url
  */
  private checkVersion() {
    // timestamp these requests to invalidate caches
    this.http.get('version.json?t=' + new Date().getTime())
      .subscribe(
        (response: any) => {
          const hash = response.hash;
          const hashChanged = this.hasHashChanged(this.currentHash, hash);
          // If new version, do something
          if (hashChanged) {
            this.error.manage(null, "Une nouvelle version de l'application est disponible. Rechargez la page.")            
          }
          // store the new hash so we wouldn't trigger versionChange again
          // only necessary in case you did not force refresh
          //this.currentHash = hash;
        },
        (err) => {
          console.error(err, 'Could not get version');
        }
      );
  }

  private hasHashChanged(currentHash, newHash) {
    if (!currentHash || currentHash === '{{POST_BUILD_ENTERS_HASH_HERE}}') {
      return false;
    }
    return currentHash !== newHash;
  }
}